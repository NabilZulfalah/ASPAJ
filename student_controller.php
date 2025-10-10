<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Auth;

class StudentController extends Controller
{
    public function dashboardStats(Request $request)
    {
        $user = Auth::user();

        $totalAssets = DB::table('assets')->count();
        $activeBorrowings = DB::table('borrowings')
            ->where('user_id', $user->id)
            ->where('status', 'approved')
            ->where('return_date', '>', now())
            ->count();
        $recentRequests = DB::table('borrowings')
            ->where('user_id', $user->id)
            ->where('created_at', '>', now()->subDays(7))
            ->count();

        return response()->json([
            'total_assets' => $totalAssets,
            'active_borrowings' => $activeBorrowings,
            'recent_requests' => $recentRequests,
        ]);
    }

    public function activeBorrowings(Request $request)
    {
        $user = Auth::user();

        $borrowings = DB::table('borrowings')
            ->join('assets', 'borrowings.asset_id', '=', 'assets.id')
            ->where('borrowings.user_id', $user->id)
            ->where('borrowings.status', 'approved')
            ->where('borrowings.return_date', '>', now())
            ->select('borrowings.*', 'assets.name as asset_name', 'assets.description as asset_description')
            ->get();

        return response()->json(['data' => $borrowings]);
    }

    public function recentRequests(Request $request)
    {
        $user = Auth::user();

        $requests = DB::table('borrowings')
            ->join('assets', 'borrowings.asset_id', '=', 'assets.id')
            ->where('borrowings.user_id', $user->id)
            ->where('borrowings.created_at', '>', now()->subDays(7))
            ->select('borrowings.*', 'assets.name as asset_name', 'assets.description as asset_description')
            ->get();

        return response()->json(['data' => $requests]);
    }

    public function borrowingHistory(Request $request)
    {
        $user = Auth::user();

        $history = DB::table('borrowings')
            ->join('assets', 'borrowings.asset_id', '=', 'assets.id')
            ->where('borrowings.user_id', $user->id)
            ->select('borrowings.*', 'assets.name as asset_name', 'assets.description as asset_description')
            ->get();

        return response()->json(['data' => $history]);
    }
}
