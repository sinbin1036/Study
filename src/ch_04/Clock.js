import React from "react";

function Clock(props) {
    const { location, timeZone } = props;
    
    const getOverseasTime = (timeZone) => {
        return new Date().toLocaleTimeString('ko-KR', { timeZone });
    };

    return(
        <div>
            <h1>{location} 현재 시간</h1>
            <h2>{getOverseasTime(timeZone)}</h2>
        </div>
    );
}

export default Clock;