using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace AutoMarket.Models
{
    public class UserRoleViewModel
    {
        public UserRoleViewModel() { }
        public UserRoleViewModel(ApplicationUser user) {
            this.user = user;   
        }
        public ApplicationUser user { get; set; }
        public string role { get; set; }
    }
}