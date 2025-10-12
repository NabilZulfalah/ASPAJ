=======
- [x] Fix type mismatch in BorrowingStatusActivity.java: change obj.getString("id") to Integer.parseInt(obj.getString("id"))
- [x] Fix returnBorrowing method: remove status set to 'returned', loop only over approved items, call updateBorrowingStatusAfterReturn
- [x] Add updateBorrowingStatusAfterReturn method
- [x] Fix adminReturn method: remove status set to 'returned', loop only over approved items, call updateBorrowingStatusAfterReturn
