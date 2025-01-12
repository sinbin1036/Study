import React from "react";

function App() {

    const products = [
        {title: 'Cabbage' ,isFruit: false ,id: 1},
        {title: 'Garlic',isFruit: false ,id: 2},
        {title: 'Apple',isFruit: true ,id: 3},
    ];

    const listItmes = products.map(products =>
        <li
            key = {products.id}
            style={{
                color : products.isFruit ? "magenta" : "darkgreen"
            }}
        >
        {products.title}
        </li>
    );


    return (
        <div>
            <ul>{listItmes}</ul>
        </div>
    );
}

export default App;