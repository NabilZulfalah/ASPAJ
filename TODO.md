# TODO: Implement Return Borrowing Functionality

## Steps to Complete:
- [x] Step 1: Check and update BorrowingItem model fillable fields for return fields (returned_quantity, condition, notes, return_date).
- [x] Step 2: Implement returnBorrowing method in PeminjamanController.php with validation, DB transaction for updating items and stock.
- [x] Step 3: Run migration for return fields if not already applied. (Attempted, but failed due to permissions; user should run 'php artisan migrate' in Laravel directory manually.)
- [ ] Step 4: Test the /api/borrowings/{id}/return endpoint with sample data.
- [ ] Step 5: Update Android ReturnFormActivity to call the correct endpoint if needed.
