import React from "react";
import { Link } from "react-router-dom";
import './TodoItem.css'

function TodoItem({id, isDone, content, createdDate, onUpdate, onDelete}) {

  function onChangeCheckBox() {
    onUpdate(id);
  }

  function onClickDelete() {
    onDelete(id);
  }

  return(
    <div className="TodoItem">
      <input className="check_col" checked = {isDone} type="checkbox" onChange={onChangeCheckBox}/>
      <div className="title_col">{content}</div>
      <div className="date_col">{new Date(createdDate).toLocaleDateString()}</div>
      <button className="btn_col" onClick={onClickDelete}>삭제</button>
      <Link className="title_col" to={`/todo/${id + 1}`}>{content}</Link>
    </div>
  )
};

export default TodoItem;