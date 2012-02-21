Ext.define('demo.view.feedback.Form', {
  extend:'Ext.form.Panel',
  alias:'widget.feedbackForm',
  // anchor:'-30px',
  // The form will submit an AJAX request to this URL when submitted
  url:'',
  defaultType:'textfield',
  bodyBorder:false,
  border:false,
  bodyPadding:0,
  fieldDefaults:{
    labelAlign:'top',
    margin:'10px 0'
  },
  defaults:{
    anchor:'0',
    margin:'12px 0'
  },
  initComponent:function () {
    this.items = [
      {
        xtype:'feedbackCombo'
      },
      {
        fieldLabel:'Form Field',
        name:'formField1',
        layout:'anchor',
        allowBlank:false
      },
      {
        fieldLabel:'Form Field',
        name:'formField2',
        allowBlank:false
      },
      {
        xtype:'textarea',
        fieldLabel:'Form Field',
        name:'textArea1'
      }

    ];

    // Reset and Submit buttons
    this.buttons = [
      {
        text:'Reset',
        handler:function () {
          this.up('form').getForm().reset();
        }
      },
      {
        text:'Submit',
        formBind:true, //only enabled once the form is valid
        disabled:true,
        handler:function () {
          var form = this.up('form').getForm();
          if (form.isValid()) {
            form.submit({
              success:function (form, action) {
                Ext.Msg.alert('Success', action.result.msg);
              },
              failure:function (form, action) {
                Ext.Msg.alert('Failed', action.result.msg);
              }
            });
          }
        }
      }
    ];

    this.callParent(arguments);
  }
});