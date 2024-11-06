import React, {useState} from "react";
import Even from "./Even";

function Counter2() {
    const [count, setCount] = useState(0);

    function plus() {
        setCount(count + 1);
        console.log(count);
    }

    function minus() {
        setCount(count - 1);
        console.log(count);
    }

    function multiply() {
        setCount(count * 2);
        console.log(count);
    }

    function divide() {
        setCount(count / 2);
        console.log(count);
    }

    function reset(){
        setCount(0);
        console.log(count);
    }

    return (
        <div>
            <h1>대충 숫자 새줌</h1>
            <p>숫자 값 : {count}</p>
            <Even number={count}></Even>
            <button onClick={plus}>+</button> <br />
            <button onClick={minus}>-</button> <br />
            <button onClick={multiply}>x2</button> <br />
            <button onClick={divide}>/2</button> <br />
            <button onClick={reset}>reset</button> <br />
        </div>
    );
 
}

export default Counter2;