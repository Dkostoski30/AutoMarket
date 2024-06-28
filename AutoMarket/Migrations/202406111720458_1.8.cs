namespace AutoMarket.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _18 : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.Images",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        Name = c.String(),
                        Listing_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Listings", t => t.Listing_ID)
                .Index(t => t.Listing_ID);
            
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.Images", "Listing_ID", "dbo.Listings");
            DropIndex("dbo.Images", new[] { "Listing_ID" });
            DropTable("dbo.Images");
        }
    }
}
