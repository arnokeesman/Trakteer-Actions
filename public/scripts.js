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

function updateAmount(change) {
    if (change) quantity.value = Math.max(1, parseInt(quantity.value) + change);

    const unit = units.find(unit => unit.id == unitID.value);
    if (!unit) {
        amount.value = '';
        return;
    }

    amount.value = unit.price * quantity.value;
}
