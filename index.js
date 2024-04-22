const express = require('express');
const app = express();
const fs = require('fs');
const port = 6883;
const dataFile = "./supports.json"
const users = require('./users.json');
const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

app.get('/', (req, res) => {
    res.send(`
    <h1>Fake Trakteer API</h1>
    <p>choose a user to support</p>
    <ul>
        ${users.map(user => `<li><a href="/donate/${user.name}">${user.name}</a></li>`).join('\n\t\t')}
    </ul>
    `);
});

app.get('/donate/:name', (req, res) => {
    const user = users.find(user => user.name === req.params.name);
    if (!user) return res.sendStatus(404);
    res.send(`
    <h1>Support ${user.name}</h1>
    <form method="POST">
        <label for="supporter">Supporter Name:</label>
        <input type="text" name="supporter" id="supporter" placeholder="supporter name"><br/><br/>
        
        <label for="message">Support Message:</label>
        <input type="text" name="message" id="message" placeholder="support message"><br/><br/>
        
        <label for="quantity">Quantity:</label>
        <input type="number" name="quantity" id="quantity" placeholder="quantity"><br/><br/>
        
        <label for="amount">Amount:</label>
        <input type="number" name="amount" id="amount" placeholder="amount"><br/><br/>
        
        <label for="unit">Unit Name:</label>
        <input type="text" name="unit" id="unit" placeholder="unit name"><br/><br/>

        <input type="submit">
    </form>
    `);
});

app.post('/donate/:name', (req, res) => {
    try {
        const user_name = req.params.name;
        const user = users.find(user => user.name === user_name);
        if (!user) return res.sendStatus(404);

        const supporter_name = req.body.supporter;
        const support_message = req.body.message;
        const quantity = parseInt(req.body.quantity);
        const amount = parseInt(req.body.amount);
        const unit_name = req.body.unit;

        if (!supporter_name || !support_message || !quantity || !amount || !unit_name) throw new Error();

        const supports = require(dataFile);
        supports.unshift({
            user_id: user.id,
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

        res.send(`thanks for your donation to ${user_name}, ${supporter_name}<br/><a href="/">[home]</a>`)
    } catch (err) {
        res.sendStatus(400);
    }
});

app.get('/users', (req, res) => {
    res.json({
        status: 'success',
        result: {
            data: users.map(user => ({ id: user.id, name: user.name }))
        }
    })
});

app.get('/supports', (req, res) => {
    const apiKey = req.headers['key'];
    if (!apiKey) return res.sendStatus(401);
    const user = users.find(user => user.apiKey === apiKey);
    if (!user) return res.sendStatus(403);
    const supports = require(dataFile);
    res.json({
        status: 'success',
        result: {
            data: supports.filter(support => support.user_id === user.id)
        }
    })
});

app.listen(port, () => {
    console.log(`Fake Trakteer API listning on http://localhost:${port}`)
});

function writeJSON(filename, data) {
    fs.writeFileSync(filename, JSON.stringify(data, null, 4));
}

if (!fs.existsSync(dataFile)) {
    fs.writeFileSync(dataFile, "[]");
}