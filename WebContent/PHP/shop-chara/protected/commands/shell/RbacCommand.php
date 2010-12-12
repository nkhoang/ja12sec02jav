<?php

class RbacCommand extends CConsoleCommand {

    private $_authManager;

    public function getHelp() {
        return <<<EOD
USAGE
rbac
DESCRIPTION
This command generates an initial RBAC authorization hierarchy.
EOD;
    }

    /**
     * Execute the action.
     * @param array command line parameters specific for this command
     */
    public function run($args) {
        if (($this->_authManager = Yii::app()->getAuthManager()) === null) {
            echo "Error: an authorization manager, named 'authManager' must be configured to use this command.\n";
            echo "If you already added 'authManager' component in application configuration,\n";
            echo "please quit and re-enter the yiic shell.\n";
            return;
        }

        echo "This command will create three roles: Owner, Member, and Reader and the following premissions:\n";
        echo "create, read, update and delete user\n";
        echo "create, read, update and delete project\n";
        echo "create, read, update and delete issue\n";
        echo "Would you like to continue? [Yes|No] ";

        if (!strncasecmp(trim(fgets(STDIN)), 'y', 1)) {
            $this->_authManager->clearAll();

            $this->_authManager->createOperation("createUser", "create a new user");
            $this->_authManager->createOperation("viewUser", "view user profile information");
            $this->_authManager->createOperation("updateUser", "updatea users information");
            $this->_authManager->createOperation("deleteUser", "removea user from a project");

            $this->_authManager->createOperation("createItem", "create a new item");
            $this->_authManager->createOperation("viewItem", "view a new item");
            $this->_authManager->createOperation("updateItem", "update item information");
            $this->_authManager->createOperation("deleteItem", "delete an item.");
            $role = $this->_authManager->createRole("guest");
            $role->addChild("viewItem");
            
            $role = $this->_authManager->createRole("user");            
            $role->addChild("guest");
            $role->addChild("updateUser");
            $role->addChild("viewUser");
            $role->addChild("updateItem");
            $role->addChild("createItem");


            $role = $this->_authManager->createRole("admin");
            $role->addChild("guest");
            $role->addChild("user");
            $role->addChild("createUser");            
            $role->addChild("deleteUser");
            $role->addChild("deleteItem");

            echo "Authorization hierarchy successfully generated.";
        }
    }

}