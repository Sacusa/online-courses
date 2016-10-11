<form action="sell.php" method="post">
    <fieldset>
        <div class="form-group">
            <h2>Symbol</h2>
            <select autofocus name="symbol">
                
                <?php foreach ($symbols as $symbol): ?>
                    <option value="<?= $symbol['symbol'] ?>"><?= $symbol['symbol'] ?></option>
                <?php endforeach ?>
            
            </select>
            <br />
            <br />
        </div>
        <div class="form-group">
            <button class="btn btn-default" type="submit">
                <span aria-hidden="true" class="glyphicon glyphicon-log-in"></span>
                Sell
            </button>
        </div>
    </fieldset>
</form>