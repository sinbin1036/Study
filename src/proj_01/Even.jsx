import React, {useEffect} from "react";

function Even(){
  useEffect(()=>{

    return() => {
      console.log("언마운트 됨")
    }
  }, []);

  return(
    <div>현재 카운트는 짝수입니다.</div>
  );
}

export default Even;