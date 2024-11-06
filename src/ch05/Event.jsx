import React from "react";

function Event() {
    function handOneClick(e) {
        console.log(e);
        console.log(e.target.name);
        alert(e.target.name + "버튼을 눌렀습니다");
    }
    return (
        <div>
            <button name="A" onClick={handOneClick}>클릭</button>
        </div>
    );
}

export default Event;