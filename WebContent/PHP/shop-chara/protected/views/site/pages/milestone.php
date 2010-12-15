<?php
$this->pageTitle = Yii::app()->name . ' - Project Milestone';
$this->breadcrumbs = array(
    'Milestone',
);
?>
<h1>Milestone</h1>
Project start date: <b><span>10/12/2010</span></b>.
<br/>
<p></p>
<h3>This week (10-14/12/2010)</h3>

<ul>
    <li>Started the project.</li>
    <li>Functions:
        <ul>
            <li>
                <div>
                    User management - add, delete, update, create user.
                    <br/>
                    - Only <b>admin - nkhoang.it</b> can access to user management.
                    <br/>
                    - <b>username</b> and <b>email</b> must be unique.
                    <br />
                    - One user can have only <i>one role</i>.
                    <br/>
                    - Update user will also remove old user role.
                    <br/>
                    - Ajax form validation.
                </div>
            </li>
            <li>
                <div>
                    Role base access control.
                    <br />
                    - Some initial roles are: <b>admin, user, guest.</b>
                    <br/>
                    - The only way to assign a role to a user is through user management screen.

                </div>
            </li>
        </ul>
    </li>
</ul>