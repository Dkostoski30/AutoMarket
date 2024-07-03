namespace AutoMarket.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _28 : DbMigration
    {
        public override void Up()
        {
            DropIndex("dbo.AspNetUsers", "UserNameIndex");
            AddColumn("dbo.AspNetUsers", "Name", c => c.String());
            AlterColumn("dbo.AspNetUsers", "UserName", c => c.String(nullable: false, maxLength: 256));
            CreateIndex("dbo.AspNetUsers", "UserName", unique: true, name: "UserNameIndex");
        }
        
        public override void Down()
        {
            DropIndex("dbo.AspNetUsers", "UserNameIndex");
            AlterColumn("dbo.AspNetUsers", "UserName", c => c.String());
            DropColumn("dbo.AspNetUsers", "Name");
            CreateIndex("dbo.AspNetUsers", "UserName", unique: true, name: "UserNameIndex");
        }
    }
}
