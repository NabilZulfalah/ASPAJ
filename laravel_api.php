<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AdminAssetController;
use App\Http\Controllers\OfficerAssetController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});


use App\Http\Controllers\API\AuthController;
use App\Http\Controllers\API\AsetController;
use App\Http\Controllers\API\UserController;
use App\Http\Controllers\API\PeminjamanController;
use App\Http\Controllers\API\KelasController;
use App\Http\Controllers\API\StudentController;

// Asset routes
Route::get('/assets', [AsetController::class, 'index']);
Route::post('/assets', [AsetController::class, 'store']);
Route::put('/assets/{id}', [AsetController::class, 'update']);
Route::delete('/assets/{id}', [AsetController::class, 'destroy']);

// User routes
Route::get('/users', [UserController::class, 'index']);
Route::post('/users', [UserController::class, 'store']);
Route::put('/users/{id}', [UserController::class, 'update']);
Route::delete('/users/{id}', [UserController::class, 'destroy']);

// Auth routes
Route::post('/login', [AuthController::class, 'login']);

// Borrowing routes
Route::get('/borrowings', [PeminjamanController::class, 'index']);
Route::get('/borrowings/pending', [PeminjamanController::class, 'getPending']);
Route::post('/borrowings/update-status', [PeminjamanController::class, 'updateStatus']);
Route::post('/borrowings/{id}/return', [PeminjamanController::class, 'returnDevice']);

// School class routes
Route::get('/school-classes', [KelasController::class, 'index']);
Route::post('/school-classes', [KelasController::class, 'store']);
Route::put('/school-classes/{id}', [KelasController::class, 'update']);
Route::delete('/school-classes/{id}', [KelasController::class, 'destroy']);

Route::middleware('auth:sanctum')->group(function () {
    // Student routes
    Route::get('/student/dashboard-stats', [StudentController::class, 'dashboardStats']);
    Route.get('/student/active-borrowings', [StudentController::class, 'activeBorrowings']);
    Route::get('/student/recent-requests', [StudentController::class, 'recentRequests']);
    Route::get('/student/borrowing-history', [StudentController::class, 'borrowingHistory']);

    // Commodities for borrowing
    Route::get('/commodities', [AsetController::class, 'commodities']);
});
