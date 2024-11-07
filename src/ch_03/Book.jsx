import React from "react";

function Book(props) {
    return(
        <div>
            <h1>{`이 책의 이름은 ${props.name} 입니다`}</h1>
            <h3>{`이 책의 가격은 ${props.price}입니다.`}</h3>
        </div>
    );

}

export default Book;