const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');
let lines = content.split('\n');

// List of dangling multiline border lines based on error logs
const linesToDelete = [
    519, 520, 521, 522,
    2853, 2854, 2855,
    3021, 3022, 3023,
    5653, 5654, 5655, 5656,
    5869, 5870, 5871, 5872,
    5975, 5976, 5977, 5978,
    6858, 6859, 6860, 6861,
    7163, 7164, 7165, 7166,
    7590, 7591, 7592, 7593
];

for(let i = 0; i < linesToDelete.length; i++) {
    lines[linesToDelete[i]-1] = ""; // 1-indexed to 0-indexed
}

// Add commas back where needed
const addCommaLines = [3324, 4789, 5503, 7267, 3948];

// Just iterating over the file to find `contentAlignment =` without a preceding comma
for (let i = 0; i < lines.length; i++) {
    let line = lines[i];
    if (line.match(/^\s*contentAlignment\s*=/)) {
        // Look back for the last non-empty line
        let j = i - 1;
        while (j >= 0 && lines[j].trim() === "") j--;
        if (j >= 0 && !lines[j].trim().endsWith(',') && !lines[j].trim().endsWith('(')) {
            lines[j] = lines[j] + ",";
            console.log("Added comma to line " + (j + 1));
        }
    }
    if (line.match(/^\s*horizontalAlignment\s*=/)) {
        let j = i - 1;
        while (j >= 0 && lines[j].trim() === "") j--;
        if (j >= 0 && !lines[j].trim().endsWith(',') && !lines[j].trim().endsWith('(')) {
            lines[j] = lines[j] + ",";
            console.log("Added comma to line " + (j + 1));
        }
    }
    if (line.match(/^\s*verticalArrangement\s*=/)) {
        let j = i - 1;
        while (j >= 0 && lines[j].trim() === "") j--;
        if (j >= 0 && !lines[j].trim().endsWith(',') && !lines[j].trim().endsWith('(')) {
            lines[j] = lines[j] + ",";
            console.log("Added comma to line " + (j + 1));
        }
    }
    if (line.match(/^\s*horizontalArrangement\s*=/)) {
        let j = i - 1;
        while (j >= 0 && lines[j].trim() === "") j--;
        if (j >= 0 && !lines[j].trim().endsWith(',') && !lines[j].trim().endsWith('(')) {
            lines[j] = lines[j] + ",";
            console.log("Added comma to line " + (j + 1));
        }
    }
    if (line.match(/^\s*verticalAlignment\s*=/)) {
        let j = i - 1;
        while (j >= 0 && lines[j].trim() === "") j--;
        if (j >= 0 && !lines[j].trim().endsWith(',') && !lines[j].trim().endsWith('(')) {
            lines[j] = lines[j] + ",";
            console.log("Added comma to line " + (j + 1));
        }
    }
}

// Write back
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', lines.join('\n'));
