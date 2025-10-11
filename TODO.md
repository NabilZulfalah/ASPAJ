# TODO: Fix "invalid borrowing or item id" error in ReturnFormActivity

## Steps to Complete
- [x] Update ReturnFormActivity.java: Change borrowingId to String, retrieve with getStringExtra, update validation
- [x] Update BorrowingDetailActivity.java: Pass item_id as int instead of String
- [x] Test the return submission functionality

## Status
Completed

# TODO: Add userClass to SharedPrefManager and save on login

## Steps to Complete
- [x] Add KEY_USER_CLASS constant in SharedPrefManager.java
- [x] Add saveUserClass(String userClass) method
- [x] Add getUserClass() method
- [x] In LoginActivity, after saving token, extract class name from response and save to SharedPref
- [x] In ProfileActivity, use getUserClass() in fallbacks, and save from API response

## Status
Completed
