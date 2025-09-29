# TODO: Integrate Borrowings API into RiwayatPeminjaman

## Step 1: Create API Endpoint
- [x] Create `API_PHP/v1/get_borrowings.php` with mysqli connection, JOINs for required fields, and filtering logic for status, jurusan, kelas, name.

## Step 2: Update Android Activity
- [x] Edit `RiwayatPeminjaman.java` to uncomment/enable Volley request, remove `loadMockData()` call, and clean up unused methods.
