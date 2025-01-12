const people = [
    { name: "Alice", age: 17 },
    { name: "Bob", age: 22 },
    { name: "Charlie", age: 16 },
    { name: "David", age: 23 },
];

const result = people
    .filter(people => people.age >= 18)
    .map(people => `${people.name} 나이 ${people.age}`);

console.log(result);