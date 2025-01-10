import React, {useState} from "react";
import Viewer from "./Viewer";
import Controller from "./Controller";
import "./App.css";    


function App() {
    const [count, setCount] = useState(0);
    function handleSetCount(value) {
        setCount(count + value);
    }
    return (
        <div className="App">
            <h1>Counter App</h1>
            <div>
                <Viewer count={count}></Viewer>
            </div>
            <div>
                <Controller handleSetCount={handleSetCount}></Controller>
            </div>
        </div>  
    );
}

export default App;