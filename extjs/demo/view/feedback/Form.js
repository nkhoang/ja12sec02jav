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
    margin:'10px 0',
    invalidCls:''
  },
  defaults:{
    anchor:'0',
    margin:'12px 0'
  },
  initComponent:function () {
    this.itemId = 'feedbackForm';
    this.id = 'feedbackForm';
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
        xtype:'box',
        margin: '0 10px',
        autoEl:{
          tag:'a',
          html:'Reset',
          href:'#'
        },
        listeners:{
          el:{
            click:{
              fn:function (e, t, eOpts) {
                var form = Ext.getCmp('feedbackForm');
                form.getForm().reset();
              }
            }
          }
        }
      },
      {
        text:'Submit',
        cls: 'blue-btn',
        handler:function () {
          var form = this.up('form').getForm();
        }
      }
    ]

    this.callParent(arguments);
  }
});