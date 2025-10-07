# TODO: Fix Excel Import for Kelas Data

## Tasks
- [ ] Fix API d:/xampp/htdocs/android/v1/addKelas.php: Correct the DB method call to $db->addKelas($name, $program_study, $description, $level);
- [ ] Fix Android app/src/main/java/com/example/androidphpmysql/KelasListActivity.java: Update sendAddKelasRequest to send correct parameter names: "name", "level", "program_study", "description".
- [ ] Test the Excel import functionality to ensure data is now added to the database successfully.
