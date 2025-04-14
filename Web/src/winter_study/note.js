const para = document.querySelector(".btn");

para.addEventListener("click", updateName);

function updateName() {
    const name = prompt("이름 뭔데");
    para.textContent = `니 이름 : ${name}`;
}