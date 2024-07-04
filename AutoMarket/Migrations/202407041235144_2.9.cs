namespace AutoMarket.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _29 : DbMigration
    {
        public override void Up()
        {
            AlterColumn("dbo.Listings", "Description", c => c.String(nullable: false, maxLength: 1000));
        }
        
        public override void Down()
        {
            AlterColumn("dbo.Listings", "Description", c => c.String(nullable: false, maxLength: 300));
        }
    }
}
