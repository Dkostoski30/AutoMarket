namespace AutoMarket.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _21 : DbMigration
    {
        public override void Up()
        {
            DropForeignKey("dbo.Images", "Listing_ID", "dbo.Listings");
            DropIndex("dbo.Images", new[] { "Listing_ID" });
            AlterColumn("dbo.Images", "Listing_ID", c => c.Int(nullable: false));
            CreateIndex("dbo.Images", "Listing_ID");
            AddForeignKey("dbo.Images", "Listing_ID", "dbo.Listings", "ID", cascadeDelete: true);
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.Images", "Listing_ID", "dbo.Listings");
            DropIndex("dbo.Images", new[] { "Listing_ID" });
            AlterColumn("dbo.Images", "Listing_ID", c => c.Int());
            CreateIndex("dbo.Images", "Listing_ID");
            AddForeignKey("dbo.Images", "Listing_ID", "dbo.Listings", "ID");
        }
    }
}
