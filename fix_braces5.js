const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /                    \}\s*\}\s*\}\s*\}\s*if \(showExpandedPlayer\) \{/;
const replacement = `                    }
                }
            }
            
        if (showExpandedPlayer) {`;

content = content.replace(regex, replacement);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
