<div>
    <table style="margin: 0px auto;">
        <tr>
            <th style="padding: 20px; border: 1px solid black">Transaction</th>
            <th style="padding: 20px; border: 1px solid black">Date/Time</th>
            <th style="padding: 20px; border: 1px solid black">Symbol</th>
            <th style="padding: 20px; border: 1px solid black">Shares</th>
            <th style="padding: 20px; border: 1px solid black">Price</th>
        </tr>
        
        <?php foreach ($rows as $row): ?>
        
            <tr>
                <td style="padding: 10px; border: 1px solid black"><?= $row["transaction"] ?></td>
                <td style="padding: 10px; border: 1px solid black"><?= $row["datetime"] ?></td>
                <td style="padding: 10px; border: 1px solid black"><?= $row["symbol"] ?></td>
                <td style="padding: 10px; border: 1px solid black"><?= $row["shares"] ?></td>
                <td style="padding: 10px; border: 1px solid black">$<?= $row["price"] ?></td>
            </tr>

        <?php endforeach ?>
        
    </table>
</div>
