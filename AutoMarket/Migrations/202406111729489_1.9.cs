namespace AutoMarket.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _19 : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.Images", "Path", c => c.String());
        }
        
        public override void Down()
        {
            DropColumn("dbo.Images", "Path");
        }
    }
}
