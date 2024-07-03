namespace AutoMarket.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _27 : DbMigration
    {
        public override void Up()
        {
            DropIndex("dbo.AspNetUsers", "UserNameIndex");
            AlterColumn("dbo.AspNetUsers", "UserName", c => c.String(nullable: false, maxLength: 256));
            CreateIndex("dbo.AspNetUsers", "UserName", unique: true, name: "UserNameIndex");
        }

        public override void Down()
        {
            DropIndex("dbo.AspNetUsers", "UserNameIndex");
            AlterColumn("dbo.AspNetUsers", "UserName", c => c.String());
            CreateIndex("dbo.AspNetUsers", "UserName", unique: true, name: "UserNameIndex");
        }
    }
}
