import React, {useState} from "react"; 

function Even(props) {
    console.log(props.number);
    return(
        <div>
            {props.number % 2 === 0 
            ? <p>짝수입니다</p>
            : <p>홀수입니다</p> }
        </div>
    );
}

export default Even;