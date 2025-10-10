# TODO: Implement Item-Level Return Functionality

## Problem
Currently, the return process only allows returning the entire borrowing, but users need to return individual items with condition and photo proof.

## Solution
Implement item-level return functionality where users can select specific approved items to return, fill condition and upload photo.

## Steps to Complete

1. **Update BorrowingCardAdapter.java**
   - [x] Add "Kembalikan" button if borrowing has borrowed items
   - [x] On click, show dialog to select which borrowed items to return
   - [x] Navigate to ReturnFormActivity with selected items

2. **Update BorrowingItemAdapter.java**
   - [x] For borrowed items, show pickup note and "Kembalikan" button
   - [x] On click, navigate to ReturnFormActivity for that specific item
   - [x] Updated BorrowingDetailActivity to pass borrowingId and item id

3. **Update ReturnFormActivity.java**
   - [x] Modify to handle multiple items or single item
   - [x] Send condition and photo for each item
   - [x] Use correct API endpoint for item-level returns
   - [x] Changed from base64 to multipart file upload

4. **Update Constants.java**
   - [x] Add endpoint for item-level returns

5. **Update PeminjamanController.php**
   - [x] Ensure returnBorrowing method is accessible via API route

6. **Test the functionality**
   - [x] Build successful - app compiles without errors
   - [x] Fixed "Kembalikan" button visibility on status page for approved/borrowed items
   - [x] Fixed parsing error in BorrowingDetailActivity by switching to general API endpoint
   - [x] Added missing 'id' field to items in StudentController API responses
   - [ ] Run app, check status page shows "Kembalikan" for approved borrowings
   - [ ] Click "Kembalikan" button - should show item selection dialog without "Error loading items"
   - [ ] Select items to return, fill form, submit
   - [ ] Verify items are marked as returned with condition and photo
