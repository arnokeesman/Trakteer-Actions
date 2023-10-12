const express = require('express');
const app = express();
const fs = require('fs');
const port = 6883;
const dataFile = "./supports.json"

app.use(express.static('public'))

app.get('/supports', (req, res) => {
    const supports = require(dataFile);
    res.json({
        status: 'success',
        result: {
            data: supports
        }
    })
});

app.get('/add', (req, res) => {
    try {
        const supporter_name = req.query.name;
        const support_message = req.query.message;
        const quantity = parseInt(req.query.quantity);
        const amount = parseInt(req.query.amount);
        const unit_name = req.query.unit;

        if (!supporter_name || !support_message || !quantity || !amount || !unit_name) throw new Error();
        
        const supports = require(dataFile);
        supports.unshift({
            supporter_name,
            support_message,
            quantity,
            amount,
            unit_name,
            updated_at: new Date().toISOString()
                .replace(/T/, ' ')
                .replace(/\..+/, '')     
        });
        writeJSON(dataFile, supports);

        res.send(`thanks for your donation ${supporter_name}<br/><a href="/">[home]</a>`)
    } catch (err) {
        res.sendStatus(400);
    }
})

app.listen(port, () => {
    console.log(`Fake Trakteer API listning on http://localhost:${port}`)
});


function writeJSON(filename, data) {
    fs.writeFileSync(filename, JSON.stringify(data, null, 4));
}

if (!fs.existsSync(dataFile)) {
    fs.writeFileSync(dataFile, "[]");
}