import React from "react";

function Viewer({count}) {
    return (
        <div>
            <div>현재 카운터 : </div>
            <p>{count}</p>
        </div>
    )
}

export default Viewer;