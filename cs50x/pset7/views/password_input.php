<form action="password.php" method="post">
    <fieldset>
        <div class="form-group">
            <input autofocus class="form-control" name="current" placeholder="Current Password" type="password"/>
        </div>
        <div class="form-group">
            <input class="form-control" name="new" placeholder="New Password" type="password"/>
        </div>
        <div class="form-group">
            <input class="form-control" name="confirm" placeholder="New Password (again)" type="password"/>
        </div>
        <div class="form-group">
            <button class="btn btn-default" type="submit">
                <span aria-hidden="true" class="glyphicon glyphicon-log-in"></span>
                Update
            </button>
        </div>
    </fieldset>
</form>