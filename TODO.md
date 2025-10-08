# TODO: Add Pagination to AssetListActivity and UserListActivity

## AssetListActivity Pagination
- [x] Update activity_asset_list.xml: Change to LinearLayout, add pagination UI at bottom (TextView, Button previous, Button next)
- [x] Update AssetAdapter.java: Add updateData(List<Asset> newList, int startIndex) method
- [x] Update AssetListActivity.java:
  - [x] Add pagination variables: currentPage, itemsPerPage, totalPages, displayedAssetList, filteredAssetList
  - [x] Add findViewById for pagination UI elements
  - [x] Change adapter initialization to use displayedAssetList
  - [x] Modify filterAssets(String query): populate filteredAssetList, calculate totalPages/currentPage, call updatePagination
  - [x] Modify filterAssetsByCriteria: similar changes
  - [x] Add updatePagination method: populate displayedAssetList, update adapter, set info text, enable/disable buttons
  - [x] Add button listeners for previous/next
  - [x] Ensure loadAssets calls filter after loading
  - [x] Update deleteAsset in AssetAdapter to call loadAssets on success

## UserListActivity Pagination
- [x] Update activity_user_list.xml: Add pagination UI at bottom
- [x] Update UserAdapter.java: Add updateData(List<User> newList, int startIndex) method
- [x] Update UserListActivity.java: Similar changes as AssetListActivity

## Testing
- [ ] Test AssetListActivity pagination with search and filter
- [ ] Test UserListActivity pagination with search and filter
- [ ] Verify button states and info text
