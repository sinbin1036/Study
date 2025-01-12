import { useParams } from "react-router-dom";

function Todo() {
    const {id} = useParams();
    console.log(id)
    return (
        <div>
            <div>Todo 페이지입니다</div>
            <div>{id}번 할 일</div>
        </div>
    )
}

export default Todo;