const replace = require('replace-in-file');

const options1 = {
  files: 'app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt',
  from: /if\s*\(isDarkMode\)\s*Color\.(?:White|Black)\s*else\s*Color\.(?:White|Black)/g,
  to: 'MaterialTheme.colorScheme.onSurface',
};

const options2 = {
  files: 'app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt',
  from: /if\s*\(isDarkMode\)\s*Color\([^)]+\)\s*else\s*Color\([^)]+\)/g,
  to: 'MaterialTheme.colorScheme.surface',
};

const options3 = {
  files: 'app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt',
  from: /if\s*\(isDarkMode\)\s*Color\([^)]+\)\s*else\s*Color\.(?:White|Black)/g,
  to: 'MaterialTheme.colorScheme.surface',
};

const options4 = {
    files: 'app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt',
    from: /if\s*\(isDarkMode\)\s*Color\.(?:White|Black)\s*else\s*Color\([^)]+\)/g,
    to: 'MaterialTheme.colorScheme.surface',
};

try {
  let results = replace.sync(options1);
  console.log('1:', results[0]?.hasChanged);
  results = replace.sync(options2);
  console.log('2:', results[0]?.hasChanged);
  results = replace.sync(options3);
  console.log('3:', results[0]?.hasChanged);
  results = replace.sync(options4);
  console.log('4:', results[0]?.hasChanged);
} catch (error) {
  console.error('Error occurred:', error);
}
