# TODO: Add Navbar and Sidebar to All Activity XMLs

## Overview
Add DrawerLayout with Toolbar and NavigationView to all activity XML layouts that don't have them yet, similar to `activity_officer_dashboard.xml` and `activity_student_dashboard.xml`.

## Activities to Update
- [ ] activity_add_asset.xml
- [ ] activity_add_kelas.xml
- [ ] activity_add_user.xml
- [ ] activity_admin_borrowing_management.xml
- [ ] activity_admin_dashboard.xml
- [ ] activity_asset_borrow.xml
- [ ] activity_asset_list.xml
- [ ] activity_borrow_confirm.xml
- [ ] activity_borrowing_detail.xml
- [ ] activity_borrowing_status.xml
- [ ] activity_change_password.xml
- [ ] activity_forgot_password.xml
- [ ] activity_image_viewer.xml
- [ ] activity_kelas_list.xml
- [ ] activity_kelas_management.xml
- [ ] activity_login.xml (skip - login screen)
- [ ] activity_main.xml
- [ ] activity_peminjaman.xml
- [ ] activity_profile.xml
- [ ] activity_return_form.xml
- [ ] activity_riwayat_peminjaman.xml
- [ ] activity_select_jurusan.xml
- [ ] activity_user_list.xml
- [ ] activity_user_management.xml

## Template Structure
Use DrawerLayout as root with:
- LinearLayout for main content
- MaterialToolbar
- ScrollView with content
- NavigationView for sidebar

## Menus to Use
- Student activities: @menu/navigation_menu
- Officer/Admin activities: @menu/officer_navigation_menu

## Java Activities to Update
Update corresponding Java activities to implement NavigationView.OnNavigationItemSelectedListener and add drawer setup code.

## Testing
- [ ] Test navigation in all activities
- [ ] Check for any layout errors
- [ ] Verify drawer opens/closes properly
