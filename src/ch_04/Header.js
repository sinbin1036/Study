import React from "react"; 

function Header(props) {
    return(
        <div>
            <h2>제 이름은 {props.name}입니다.</h2>
            <h3>제가 사는 곳은 {props.location}입니다.</h3>
        </div>
    );
}

export default Header;