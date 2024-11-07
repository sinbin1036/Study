import React from "react";
import Book from "./Book";

function Library() {
    return(
        <div>
            <Book nmae="자바스크립트 끝내기" price={3000}></Book>
            <Book></Book>
        </div>
    );
}

export default Library;