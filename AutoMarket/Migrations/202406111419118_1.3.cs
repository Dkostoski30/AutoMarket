namespace AutoMarket.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _13 : DbMigration
    {
        public override void Up()
        {
            AlterColumn("dbo.Listings", "Title", c => c.String(nullable: false));
            AlterColumn("dbo.Listings", "Description", c => c.String(nullable: false));
        }
        
        public override void Down()
        {
            AlterColumn("dbo.Listings", "Description", c => c.String(nullable: false, maxLength: 300));
            AlterColumn("dbo.Listings", "Title", c => c.String(nullable: false, maxLength: 100));
        }
    }
}
