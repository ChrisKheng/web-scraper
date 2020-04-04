using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using PAT.Common.Classes.Expressions.ExpressionClass;

//the namespace must be PAT.Lib, the class and method names can be arbitrary
namespace PAT.Lib
{    	
    // Lists is a class which models an array of lists.
    // It can be used to model the collection of queues of crawler and 
    // the collection of buffers.        
    public class IndexURLTree : ExpressionValue
    {
        public List<int> tree;
        public int[] a;

        // Needs a default constructor.
        public IndexURLTree() {
            this.tree = new List<int>();
        }

        // lists: an array of lists to be deep copied into the new Lists object.
        public IndexURLTree(List<int> tree) {
            List<int> clone = new List<int>();
            
            foreach(int i in tree) {
                clone.Add(i);
            }
            
            this.tree = clone;
        }

        // Appends all the elements in the given array to the end of the 
        // target list.
        // listNumber: the index of the target list.        
        // Needs to return something for PAT code to work (i.e. cannot return void)
        public bool AddToTree(int url) {
            this.tree.Add(url);
			return true;
        }

        // Adds the given url into the target list.        
        // listNumber: the index of the target list. (index starts from 0)
        // url: the url to be added into the target list.        
        public bool exists(int url) {
            return tree.Contains(url);
        }
        
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
