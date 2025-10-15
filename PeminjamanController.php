<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Models\Borrowing;
use App\Models\BorrowingItem;
use App\Models\Commodity;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;

class PeminjamanController extends Controller
{
    public function index(Request $request)
    {
        $user = $request->user();

        if ($user->isAdmin()) {
            $borrowings = Borrowing::with('student.user', 'student.schoolClass', 'items.commodity')
                ->orderBy('created_at', 'desc')
                ->get();
        } elseif ($user->isOfficer()) {
            $query = Borrowing::with('student.user', 'student.schoolClass', 'items.commodity')
                ->whereHas('items.commodity', function($q) use ($user) {
                    $q->where('jurusan', $user->jurusan);
                });
            $borrowings = $query->orderBy('created_at', 'desc')->get();
        } else {
            // Student
            $borrowings = Borrowing::with('student.user', 'student.schoolClass', 'items.commodity')
                ->where('student_id', $user->student->id)
                ->orderBy('created_at', 'desc')
                ->get();
        }

        return response()->json([
            'success' => true,
            'message' => 'Borrowings retrieved successfully',
            'data' => $borrowings
        ]);
    }

    public function getPending(Request $request)
    {
        $user = $request->user();

        if ($user->isAdmin()) {
            $borrowings = Borrowing::with('student.user', 'student.schoolClass', 'items.commodity')
                ->where('status', 'pending')
                ->orderBy('created_at', 'desc')
                ->get();
        } elseif ($user->isOfficer()) {
            $borrowings = Borrowing::with('student.user', 'student.schoolClass', 'items.commodity')
                ->where('status', 'pending')
                ->whereHas('items.commodity', function($q) use ($user) {
                    $q->where('jurusan', $user->jurusan);
                })
                ->orderBy('created_at', 'desc')
                ->get();
        } else {
            $borrowings = Borrowing::with('student.user', 'student.schoolClass', 'items.commodity')
                ->where('status', 'pending')
                ->where('student_id', $user->student->id)
                ->orderBy('created_at', 'desc')
                ->get();
        }

        return response()->json([
            'success' => true,
            'message' => 'Pending borrowings retrieved successfully',
            'data' => $borrowings
        ]);
    }

    public function updateStatus(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'borrowing_id' => 'required|exists:borrowings,id',
            'status' => 'required|in:approved,rejected',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $user = $request->user();
        $borrowing = Borrowing::find($request->borrowing_id);

        // Check permissions
        if ($user->isOfficer()) {
            // Officer can only update borrowings for their jurusan
            $hasPermission = $borrowing->items->contains(function($item) use ($user) {
                return $item->commodity->jurusan === $user->jurusan;
            });
            if (!$hasPermission) {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized to update this borrowing'
                ], 403);
            }
        }

        $borrowing->status = $request->status;
        $borrowing->save();

        return response()->json([
            'success' => true,
            'message' => 'Borrowing status updated successfully',
            'data' => $borrowing->load('student.user', 'student.schoolClass', 'items.commodity')
        ]);
    }

    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'borrow_date' => 'required|date|after_or_equal:today',
            'return_date' => 'required|date|after:borrow_date',
            'tujuan' => 'required|string|max:255',
            'items' => 'required|array|min:1',
            'items.*.commodity_id' => 'required|exists:commodities,id',
            'items.*.quantity' => 'required|integer|min:1',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $user = $request->user();

        // Check if student has any active borrowings
        $activeBorrowings = Borrowing::where('student_id', $user->student->id)
            ->whereIn('status', ['pending', 'approved'])
            ->count();

        if ($activeBorrowings > 0) {
            return response()->json([
                'success' => false,
                'message' => 'You have active borrowings. Please return them first.'
            ], 400);
        }

        DB::beginTransaction();
        try {
            // Check item availability
            foreach ($request->items as $item) {
                $commodity = Commodity::find($item['asset_id']);
                if ($commodity->quantity < $item['quantity']) {
                    DB::rollBack();
                    return response()->json([
                        'success' => false,
                        'message' => 'Insufficient quantity for ' . $commodity->name
                    ], 400);
                }
            }

            // Create borrowing
            $borrowing = Borrowing::create([
                'student_id' => $user->student->id,
                'borrow_date' => $request->borrow_date,
                'return_date' => $request->return_date,
                'status' => 'pending',
                'tujuan' => $request->tujuan,
            ]);

            // Create borrowing items
            foreach ($request->items as $item) {
                BorrowingItem::create([
                    'borrowing_id' => $borrowing->id,
                    'commodity_id' => $item['asset_id'],
                    'quantity' => $item['quantity'],
                ]);

                // Update commodity quantity
                $commodity = Commodity::find($item['asset_id']);
                $commodity->quantity -= $item['quantity'];
                $commodity->save();
            }

            DB::commit();
            return response()->json([
                'success' => true,
                'message' => 'Borrowing request created successfully',
                'data' => $borrowing->load('student.user', 'student.schoolClass', 'items.commodity')
            ], 201);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to create borrowing: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function show(Request $request, $id)
    {
        $user = $request->user();
        $borrowing = Borrowing::with('student.user', 'student.schoolClass', 'items.commodity');

        if ($user->isOfficer()) {
            $borrowing->whereHas('items.commodity', function($q) use ($user) {
                $q->where('jurusan', $user->jurusan);
            });
        } elseif (!$user->isAdmin()) {
            $borrowing->where('student_id', $user->student->id);
        }

        $borrowing = $borrowing->find($id);

        if (!$borrowing) {
            return response()->json([
                'success' => false,
                'message' => 'Borrowing not found'
            ], 404);
        }

        return response()->json([
            'success' => true,
            'message' => 'Borrowing retrieved successfully',
            'data' => $borrowing
        ]);
    }

    public function returnBorrowing(Request $request, $id)
    {
        $validator = Validator::make($request->all(), [
            'items' => 'nullable|array',
            'items.*.borrowing_item_id' => 'required_with:items|exists:borrowing_items,id',
            'items.*.quantity' => 'required_with:items|integer|min:1',
            'items.*.return_condition' => 'required_with:items|string',
            'items.*.return_photo' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
            'return_condition' => 'required_without:items|string',
            'return_photo' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $user = $request->user();
        $borrowing = Borrowing::with('items.commodity')->find($id);

        if (!$borrowing) {
            return response()->json([
                'success' => false,
                'message' => 'Borrowing not found'
            ], 404);
        }

        // Check if user owns this borrowing
        if ($borrowing->student_id !== $user->student->id) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized'
            ], 403);
        }

        // Check if borrowing is approved or partially_returned
        if (!in_array($borrowing->status, ['approved', 'partially_returned'])) {
            return response()->json([
                'success' => false,
                'message' => 'Borrowing is not approved or partially returned'
            ], 400);
        }

        DB::beginTransaction();
        try {
            if ($request->has('items') && !empty($request->items)) {
                // Partial return
                foreach ($request->items as $index => $returnItem) {
                    $borrowingItem = $borrowing->items->find($returnItem['borrowing_item_id']);
                    if (!$borrowingItem) {
                        DB::rollBack();
                        return response()->json([
                            'success' => false,
                            'message' => 'Invalid borrowing item'
                        ], 400);
                    }
                    if ($borrowingItem->status === 'returned') {
                        continue; // Already returned
                    }
                    if ($returnItem['quantity'] > $borrowingItem->quantity) {
                        DB::rollBack();
                        return response()->json([
                            'success' => false,
                            'message' => 'Return quantity exceeds borrowed quantity'
                        ], 400);
                    }

                    // Handle photo upload for item
                    $photoPath = $request->hasFile("items.{$index}.return_photo") ? $request->file("items.{$index}.return_photo")->store('return_photos', 'public') : null;

                    // Update borrowing item
                    $borrowingItem->status = 'returned';
                    $borrowingItem->return_condition = $returnItem['return_condition'];
                    $borrowingItem->return_photo = $photoPath;
                    $borrowingItem->save();

                    // Return quantity to inventory
                    $commodity = $borrowingItem->commodity;
                    $commodity->quantity += $returnItem['quantity'];
                    $commodity->save();
                }
            } else {
                // Full return
                foreach ($borrowing->items as $item) {
                    if ($item->status === 'returned') continue;

                    // Handle photo upload
                    $photoPath = $request->hasFile('return_photo') ? $request->file('return_photo')->store('return_photos', 'public') : null;

                    // Update borrowing item
                    $item->status = 'returned';
                    $item->return_condition = $request->return_condition;
                    $item->return_photo = $photoPath;
                    $item->save();

                    // Return quantity to inventory
                    $commodity = $item->commodity;
                    $commodity->quantity += $item->quantity;
                    $commodity->save();
                }
            }

            // Update borrowing status
            $allReturned = $borrowing->items->every(function($item) {
                return $item->status === 'returned';
            });
            $borrowing->status = $allReturned ? 'returned' : 'partially_returned';
            $borrowing->save();

            DB::commit();
            return response()->json([
                'success' => true,
                'message' => 'Borrowing returned successfully',
                'data' => $borrowing->load('student.user', 'student.schoolClass', 'items.commodity')
            ]);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to return borrowing: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function approve(Request $request, $id)
    {
        $user = $request->user();
        $borrowing = Borrowing::with('items.commodity')->find($id);

        if (!$borrowing) {
            return response()->json([
                'success' => false,
                'message' => 'Borrowing not found'
            ], 404);
        }

        // Check permissions
        if ($user->isOfficer()) {
            $hasPermission = $borrowing->items->contains(function($item) use ($user) {
                return $item->commodity->jurusan === $user->jurusan;
            });
            if (!$hasPermission) {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized to approve this borrowing'
                ], 403);
            }
            // Officers can only approve if borrowing has 1 or fewer items
            if ($borrowing->items->count() > 1) {
                return response()->json([
                    'success' => false,
                    'message' => 'Only admin can approve borrowings with more than 1 item'
                ], 403);
            }
        }

        if ($borrowing->status !== 'pending') {
            return response()->json([
                'success' => false,
                'message' => 'Borrowing is not pending'
            ], 400);
        }

        $borrowing->status = 'approved';
        $borrowing->save();

        return response()->json([
            'success' => true,
            'message' => 'Borrowing approved successfully',
            'data' => $borrowing->load('student.user', 'student.schoolClass', 'items.commodity')
        ]);
    }

    public function reject(Request $request, $id)
    {
        $user = $request->user();
        $borrowing = Borrowing::with('items.commodity')->find($id);

        if (!$borrowing) {
            return response()->json([
                'success' => false,
                'message' => 'Borrowing not found'
            ], 404);
        }

        // Check permissions
        if ($user->isOfficer()) {
            $hasPermission = $borrowing->items->contains(function($item) use ($user) {
                return $item->commodity->jurusan === $user->jurusan;
            });
            if (!$hasPermission) {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized to reject this borrowing'
                ], 403);
            }
        }

        if ($borrowing->status !== 'pending') {
            return response()->json([
                'success' => false,
                'message' => 'Borrowing is not pending'
            ], 400);
        }

        DB::beginTransaction();
        try {
            // Return items to inventory
            foreach ($borrowing->items as $item) {
                $commodity = $item->commodity;
                $commodity->quantity += $item->quantity;
                $commodity->save();
            }

            $borrowing->status = 'rejected';
            $borrowing->save();

            DB::commit();
            return response()->json([
                'success' => true,
                'message' => 'Borrowing rejected successfully',
                'data' => $borrowing->load('student.user', 'student.schoolClass', 'items.commodity')
            ]);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to reject borrowing: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    public function adminReturn(Request $request, $id)
    {
        $validator = Validator::make($request->all(), [
            'items' => 'nullable|array',
            'items.*.borrowing_item_id' => 'required_with:items|exists:borrowing_items,id',
            'items.*.quantity' => 'required_with:items|integer|min:1',
            'items.*.return_condition' => 'required_with:items|string',
            'items.*.return_photo' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
            'return_condition' => 'required_without:items|string',
            'return_photo' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $borrowing = Borrowing::with('items.commodity')->find($id);

        if (!$borrowing) {
            return response()->json([
                'success' => false,
                'message' => 'Borrowing not found'
            ], 404);
        }

        // Check if borrowing is approved or partially_returned
        if (!in_array($borrowing->status, ['approved', 'partially_returned'])) {
            return response()->json([
                'success' => false,
                'message' => 'Borrowing is not approved or partially returned'
            ], 400);
        }

        DB::beginTransaction();
        try {
            if ($request->has('items') && !empty($request->items)) {
                // Partial return
                foreach ($request->items as $index => $returnItem) {
                    $borrowingItem = $borrowing->items->find($returnItem['borrowing_item_id']);
                    if (!$borrowingItem) {
                        DB::rollBack();
                        return response()->json([
                            'success' => false,
                            'message' => 'Invalid borrowing item'
                        ], 400);
                    }
                    if ($borrowingItem->status === 'returned') {
                        continue; // Already returned
                    }
                    if ($returnItem['quantity'] > $borrowingItem->quantity) {
                        DB::rollBack();
                        return response()->json([
                            'success' => false,
                            'message' => 'Return quantity exceeds borrowed quantity'
                        ], 400);
                    }

                    // Handle photo upload for item
                    $photoPath = $request->hasFile("items.{$index}.return_photo") ? $request->file("items.{$index}.return_photo")->store('return_photos', 'public') : null;

                    // Update borrowing item
                    $borrowingItem->status = 'returned';
                    $borrowingItem->return_condition = $returnItem['return_condition'];
                    $borrowingItem->return_photo = $photoPath;
                    $borrowingItem->save();

                    // Return quantity to inventory
                    $commodity = $borrowingItem->commodity;
                    $commodity->quantity += $returnItem['quantity'];
                    $commodity->save();
                }
            } else {
                // Full return
                foreach ($borrowing->items as $item) {
                    if ($item->status === 'returned') continue;

                    // Handle photo upload
                    $photoPath = $request->hasFile('return_photo') ? $request->file('return_photo')->store('return_photos', 'public') : null;

                    // Update borrowing item
                    $item->status = 'returned';
                    $item->return_condition = $request->return_condition;
                    $item->return_photo = $photoPath;
                    $item->save();

                    // Return quantity to inventory
                    $commodity = $item->commodity;
                    $commodity->quantity += $item->quantity;
                    $commodity->save();
                }
            }

            // Update borrowing status
            $allReturned = $borrowing->items->every(function($item) {
                return $item->status === 'returned';
            });
            $borrowing->status = $allReturned ? 'returned' : 'partially_returned';
            $borrowing->save();

            DB::commit();
            return response()->json([
                'success' => true,
                'message' => 'Borrowing returned successfully',
                'data' => $borrowing->load('student.user', 'student.schoolClass', 'items.commodity')
            ]);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to return borrowing: ' . $e->getMessage(),
                'data' => null
            ], 500);
        }
    }
}
