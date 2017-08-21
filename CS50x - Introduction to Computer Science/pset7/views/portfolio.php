<div>
    <table style="margin: 0px auto;">
        <tr>
            <th style="padding: 20px; border: 1px solid black">Symbol</th>
            <th style="padding: 20px; border: 1px solid black">Name</th>
            <th style="padding: 20px; border: 1px solid black">Shares</th>
            <th style="padding: 20px; border: 1px solid black">Price</th>
            <th style="padding: 20px; border: 1px solid black">TOTAL</th>
        </tr>
        
        <?php foreach ($positions as $position): ?>
        
            <tr>
                <td style="padding: 10px; border: 1px solid black"><?= $position["symbol"] ?></td>
                <td style="padding: 10px; border: 1px solid black"><?= $position["name"] ?></td>
                <td style="padding: 10px; border: 1px solid black"><?= $position["shares"] ?></td>
                <td style="padding: 10px; border: 1px solid black">$<?= $position["price"] ?></td>
                <td style="padding: 10px; border: 1px solid black">$<?= $position["total"] ?></td>
            </tr>

        <?php endforeach ?>
        
        <tr>
            <td style="padding: 10px; border: 1px solid black">CASH</td>
            <td style="padding: 10px; border-bottom: 1px solid black"></td>
            <td style="padding: 10px; border-bottom: 1px solid black"></td>
            <td style="padding: 10px; border-bottom: 1px solid black"></td>
            <td style="padding: 10px; border: 1px solid black">$<?= $cash ?></td>
        </tr>
    </table>
</div>
