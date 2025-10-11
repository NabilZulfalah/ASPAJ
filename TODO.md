# TODO: Add Laravel Logs to API Controllers

## Overview
Add Laravel logging to all public methods in API controllers for debugging purposes. This includes importing the Log facade and adding Log::info() statements at the start and end of each method, plus Log::error() for exceptions and errors.

## Controllers to Update
- PeminjamanController.php (6 methods: index, getPending, show, updateStatus, store, returnBorrowing)
- StudentController.php (5 methods: dashboardStats, activeBorrowings, recentRequests, borrowingHistory, showBorrowing)
- AuthController.php (1 method: login)
- KelasController.php (4 methods: index, store, update, destroy)
- UserController.php (4 methods: index, store, update, destroy)
- AsetController.php (5 methods: index, store, update, destroy, commodities)

## Steps
1. [x] Add `use Illuminate\Support\Facades\Log;` to PeminjamanController.php
2. [x] Add logging to all methods in PeminjamanController.php
3. [x] Add `use Illuminate\Support\Facades\Log;` to StudentController.php
4. [x] Add logging to all methods in StudentController.php
5. [x] Add `use Illuminate\Support\Facades\Log;` to AuthController.php
6. [x] Add logging to login method in AuthController.php
7. [x] Add `use Illuminate\Support\Facades\Log;` to KelasController.php
8. [x] Add logging to all methods in KelasController.php
9. [x] Add `use Illuminate\Support\Facades\Log;` to UserController.php
10. [x] Add logging to all methods in UserController.php
11. [x] Add `use Illuminate\Support\Facades\Log;` to AsetController.php
12. [x] Add logging to all methods in AsetController.php
13. [x] Add Log::error() for exceptions and error cases in PeminjamanController (returnBorrowing method updated with error logs for status not approved, unauthorized, validation errors, and exceptions)
14. [x] Fix validation error "The items field is required" in returnBorrowing by changing Android app to send single item parameters (item_id, condition, description, photo) instead of nested array, and updated controller validation and logic accordingly.
15. [x] Test logging by making API calls and checking Laravel log files (logging confirmed working, validation error fixed by increasing photo max size to 10000 KB)

## Notes
- Log messages: 'ControllerName::methodName started' at beginning, 'ControllerName::methodName ended' before each return
- Log errors: 'ControllerName::methodName error: message' in catch blocks or error returns
- Ensure logs help debug issues like 400 errors in borrowing/return submissions
