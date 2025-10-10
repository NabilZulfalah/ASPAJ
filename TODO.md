# TODO: Fix photo display in borrowing status and detail pages

- [x] Fix field name mismatch: Android expecting "photo_url" but API returning "photo"
- [x] Add GET /student/borrowings/{id} route for individual borrowing details
- [x] Update StudentController to return "photo" field with full URL
- [x] Update Android code to use "photo" field and handle full URLs
- [x] Fix double slash in photo URLs by using ltrim($photo, '/') in API controllers
- [x] Test photo loading in borrowing status and detail activities
