namespace AutoMarket.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _12 : DbMigration
    {
        public override void Up()
        {
            AlterColumn("dbo.Listings", "Title", c => c.String(nullable: false, maxLength: 100));
            AlterColumn("dbo.Listings", "Description", c => c.String(nullable: false, maxLength: 300));
        }
        
        public override void Down()
        {
            AlterColumn("dbo.Listings", "Description", c => c.String(nullable: false));
            AlterColumn("dbo.Listings", "Title", c => c.String(nullable: false));
        }
    }
}
