using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using PAT.Common.Classes.Expressions.ExpressionClass;

//the namespace must be PAT.Lib, the class and method names can be arbitrary
namespace PAT.Lib
{
    /// <summary>
    /// The math library that can be used in your model.
    /// all methods should be declared as public static.
    /// 
    /// The parameters must be of type "int", or "int array"
    /// The number of parameters can be 0 or many
    /// 
    /// The return type can be bool, int or int[] only.
    /// 
    /// The method name will be used directly in your model.
    /// e.g. call(max, 10, 2), call(dominate, 3, 2), call(amax, [1,3,5]),
    /// 
    /// Note: method names are case sensetive
    /// </summary>
    public class Lists : ExpressionValue
    {
		public List<int>[] lists;

		public Lists(int numLists) {
			this.lists = new List<int>[numLists];
		}

		public Lists(List<int>[] lists) {
			List<int>[] clone = new List<int>[lists.Length];
			
			for (int i = 0; i < lists.Length; i++) {
				clone[i] = lists[i].ToList();
			}
			
			this.lists = clone;
		}

		public void AddToList(int listNumber, int url) {
			lists[listNumber].Add(url);
		}
		
		public int PollFromList(int listNumber) {
			int head = lists[listNumber][0];
			lists[listNumber].RemoveAt(0);
			
			return head;
		}
		
		public bool isListEmpty(int listNumber) {
			return !(lists[listNumber].Any());
		}

        /// <summary>
        /// Please implement this method to provide the string representation of the datatype
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            return ExpressionID;
        }


        /// <summary>
        /// Please implement this method to return a deep clone of the current object
        /// </summary>
        /// <returns></returns>
        public override ExpressionValue GetClone()
        {
            return new Lists(this.lists);
        }


        /// <summary>
        /// Please implement this method to provide the compact string representation of the datatype
        /// </summary>
        /// <returns></returns>
        public override string ExpressionID
        {
            get {
            	String result = "";
            	
            	foreach (List<int> list in this.lists) {
            		result += "[" + String.Join(", ", list) + "], ";
            	}
            	
            	if (result.Length > 0) {
            		result.Remove(result.Length - 2, 2);
            	}
            	
            	return result; 
            }
        }

    }
}
