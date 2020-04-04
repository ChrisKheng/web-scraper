using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using PAT.Common.Classes.Expressions.ExpressionClass;

//the namespace must be PAT.Lib, the class and method names can be arbitrary
namespace PAT.Lib
{    	  
    public class IndexURLTree : ExpressionValue
    {
        public List<int> tree;
        public int[] a;

        // Needs a default constructor.
        public IndexURLTree() {
            this.tree = new List<int>();
        }

        // tree: a list to be deep copied into the new IndexURLTree object.
        public IndexURLTree(List<int> tree) {
            List<int> clone = new List<int>();
            
            foreach(int i in tree) {
                clone.Add(i);
            }
            
            this.tree = clone;
        }

        // Add url into tree
        // url: the url to be added into the the tree.  
        public bool AddToTree(int url) {
            this.tree.Add(url);
			return true;
        }

        // Check if the url exists in the tree        
        // url: the url to see if exist.        
        public bool exists(int url) {
            return tree.Contains(url);
        }
        
        // Check if any duplicate exist in the tree.
        public bool duplicateExist() {
            return tree.GroupBy(x => x).Any(g => g.Count() > 1);
        }
      
        //---------------------------- For system use ------------------------------
        /// Returns the  string representation of the datatype.
        public override string ToString() {
            return ExpressionID;
        }
        
        /// Returns a deep clone of the current object.
        public override ExpressionValue GetClone()
        {
            return new IndexURLTree(this.tree);
        }
        
        /// Returns the compact string representation of the datatype.
        public override string ExpressionID
        {
            get {
                String result = "";
                
                
                result += "[" + String.Join(", ", this.tree.Select(i => i.ToString()).ToArray()) + "], ";
                
                
                
                return result; 
            }
        }
    }
}
