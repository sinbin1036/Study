import React from "react";

function App() {
    const students = [
        {name: "John", scores: [75, 85, 80] },
        {name: "Alice", scores: [95, 90, 91] },
        {name: "Bob", scores: [70, 75, 80] },
    ];
    
    const result = students.map(student => {
        const average = student.scores.reduce((sum, score) => sum + score, 0) / student.scores.length

        return `${student.name} : ${average} `
    });


    return (
        <div>
            {result}
        </div>
    )


}

export default App;