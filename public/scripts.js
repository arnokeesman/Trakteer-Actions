function toggleCustomName() {
    const select = document.getElementById('supporterNameSelect');
    const customNameInput = document.getElementById('supporterName');
    if (select.value === 'custom') {
        customNameInput.style.display = 'block';
        customNameInput.required = true;
    } else {
        customNameInput.style.display = 'none';
        customNameInput.required = false;
    }
}

function changeQuantity(amount) {
    const quantityInput = document.getElementById('quantity');
    const currentQuantity = parseInt(quantityInput.value);
    const newQuantity = currentQuantity + amount;
    if (newQuantity >= 1) {
        quantityInput.value = newQuantity;
    }
}
