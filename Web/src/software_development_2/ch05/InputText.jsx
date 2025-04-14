import React, {useState} from "react";

function InputText() {
    const handOneChange = (e) => {
        console.log(e.target.value);
    }

    return (
        <div>
            <input onChange= {handOneChange} />
        </div>
    );
}

export default InputText;