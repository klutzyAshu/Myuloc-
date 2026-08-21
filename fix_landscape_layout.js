const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

// 1. Add GlobalNavigationRail composable if not exists
if (!content.includes('fun GlobalNavigationRail')) {
    const navRailCode = `
@Composable
fun GlobalNavigationRail(
    currentTab: String,
    isDarkMode: Boolean,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        Triple("all", "Home", Icons.Default.Home),
        Triple("search", "Library", Icons.Default.Folder),
        Triple("favorites", "Favorites", Icons.Default.Favorite)
    )

    val activeColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    val inactiveColor = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    val bgColor = androidx.compose.material3.MaterialTheme.colorScheme.background.copy(alpha = 0.8f)

    androidx.compose.material3.NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp),
        containerColor = bgColor
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items.forEach { (tabId, tabName, icon) ->
                val isSelected = currentTab == tabId
                androidx.compose.material3.NavigationRailItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tabId) },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = tabName,
                            tint = if (isSelected) activeColor else inactiveColor
                        )
                    },
                    label = {
                        Text(
                            text = tabName,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) activeColor else inactiveColor
                        )
                    },
                    colors = androidx.compose.material3.NavigationRailItemDefaults.colors(
                        selectedIconColor = activeColor,
                        unselectedIconColor = inactiveColor,
                        selectedTextColor = activeColor,
                        unselectedTextColor = inactiveColor,
                        indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
`;
    // append to bottom
    content += navRailCode;
}

// 2. Main column padding
const columnOld = `                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        
                        .zIndex(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {`;

const columnNew = `                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .padding(
                            start = if (isLandscape) 96.dp else 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                ) {`;
content = content.replace(columnOld, columnNew);

// 3. AnchoredPlayerBarWrapper padding
const anchoredOld = `                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 72.dp) // Leave space for BottomNavigationBar
                        .zIndex(10f)`;
const anchoredNew = `                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            bottom = if (isLandscape) 16.dp else 72.dp,
                            start = if (isLandscape) 80.dp else 0.dp
                        ) // Leave space for BottomNavigationBar in portrait
                        .zIndex(10f)`;
content = content.replace(anchoredOld, anchoredNew);

// 4. GlobalBottomNavigationBar
const bottomNavOld = `                // Global Bottom Navigation Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .zIndex(20f)
                ) {
                    GlobalBottomNavigationBar(
                        currentTab = currentTab,
                        isDarkMode = isDarkMode,
                        onTabSelected = { viewModel.setTab(it) }
                    )
                }`;
const bottomNavNew = `                // Global Navigation (Bottom in Portrait, Rail in Landscape)
                Box(
                    modifier = Modifier
                        .align(if (isLandscape) Alignment.CenterStart else Alignment.BottomCenter)
                        .fillMaxWidth(if (isLandscape) 0f else 1f)
                        .fillMaxHeight(if (isLandscape) 1f else 0f)
                        .zIndex(20f)
                ) {
                    if (isLandscape) {
                        GlobalNavigationRail(
                            currentTab = currentTab,
                            isDarkMode = isDarkMode,
                            onTabSelected = { viewModel.setTab(it) }
                        )
                    } else {
                        GlobalBottomNavigationBar(
                            currentTab = currentTab,
                            isDarkMode = isDarkMode,
                            onTabSelected = { viewModel.setTab(it) }
                        )
                    }
                }`;
content = content.replace(bottomNavOld, bottomNavNew);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
