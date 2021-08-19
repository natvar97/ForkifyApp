package com.indialone.forkifyapp

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.indialone.forkifyapp.adapters.CustomListItemAdapter
import com.indialone.forkifyapp.adapters.RecipeItemAdapter
import com.indialone.forkifyapp.databinding.DialogCustomListBinding
import com.indialone.forkifyapp.databinding.FragmentForkListBinding
import com.indialone.forkifyapp.model.details.Recipe
import com.indialone.forkifyapp.model.search.RecipesItem
import com.indialone.forkifyapp.utils.Constants
import com.indialone.forkifyapp.viewmodel.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ForkListFragment : Fragment() {

    private lateinit var mBinding: FragmentForkListBinding
    private lateinit var recipeViewModel: RecipeViewModel
    private var queries = getSearchOptions()
    private var selectedQuery: String = ""
    private lateinit var mCustomListDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentForkListBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeViewModel =
            ViewModelProvider(this, Injection.provideViewModelFactory(requireContext(), this)).get(
                RecipeViewModel::class.java
            )

        if (selectedQuery == "") {
            mBinding.tv.visibility = View.VISIBLE
            mBinding.tv.text = "to load your Favourite Category Dishes"
            mBinding.tvIcon.visibility = View.VISIBLE
            mBinding.tvIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.filter,
                0,
                0,
                0
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBinding.tvIcon.compoundDrawableTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.white))
            }
            mBinding.rvRecipes.visibility = View.GONE
        } else {
            mBinding.tv.visibility = View.GONE
            mBinding.tvIcon.visibility = View.GONE
            mBinding.rvRecipes.visibility = View.VISIBLE
            // i comment below line because it reloads it again when i come back from ForkRecipeDetailsFragment.kt
            // recipeViewModel.fetchSearchRecipes(selectedQuery)
            recipeViewModel.getSearchRecipes().observe(viewLifecycleOwner) { response ->
                Log.e("response count", "${response.count}")
                Log.e("response", "${response.recipes}")
                mBinding.rvRecipes.layoutManager = LinearLayoutManager(mBinding.root.context)
                mBinding.rvRecipes.adapter =
                    RecipeItemAdapter()
            }
        }

    }

    private fun FragmentForkListBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {


        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect()
        }
    }

    private fun FragmentForkListBinding.bindState(
        uiState: StateFlow<UiState>,
        uiActions: (UiAction) -> Unit
    ) {

    }

    private fun FragmentForkListBinding.bindList(
        repoAdapter: RecipeItemAdapter,
        uiState: StateFlow<UiState>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        rvRecipes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = repoAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for RemoteMediator changes.
            .distinctUntilChangedBy { it.refresh }
            // Only react to cases where Remote REFRESH completes i.e., NotLoading.
            .map { it.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        val pagingData = uiState
            .map { it.pagingData }
            .distinctUntilChanged()

        lifecycleScope.launch {
            combine(shouldScrollToTop, pagingData, ::Pair)
                // Each unique PagingData should be submitted once, take the latest from
                // shouldScrollToTop
                .distinctUntilChangedBy { it.second }
                .collectLatest { (shouldScroll, pagingData) ->
                    repoAdapter.submitData(pagingData as PagingData<UiModel>)
                    // Scroll only after the data has been submitted to the adapter,
                    // and is a fresh search
                    if (shouldScroll) rvRecipes.scrollToPosition(0)
                }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.iv_filter -> {
                filterDishesListDialog()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun filterDishesListDialog() {
        mCustomListDialog = Dialog(requireActivity())

        val binding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)

        val dishes = getSearchOptions()
        binding.tvTitle.text = Constants.TITLE_DIALOG
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvList.adapter = CustomListItemAdapter(
            requireActivity(),
            this,
            dishes,
            Constants.FILTER_SELECTION
        )
        mCustomListDialog.show()
    }

    fun filterSelection(filterItemSelection: String) {
        mCustomListDialog.dismiss()
        Log.e("Filter Selection", filterItemSelection)
        mBinding.tv.visibility = View.GONE
        mBinding.tvIcon.visibility = View.GONE
        mBinding.rvRecipes.visibility = View.VISIBLE
        recipeViewModel.fetchSearchRecipes(filterItemSelection)
        recipeViewModel.getSearchRecipes().observe(viewLifecycleOwner) { response ->
            Log.e("response count", "${response.count}")
            Log.e("response", "${response.recipes}")
            mBinding.rvRecipes.layoutManager = LinearLayoutManager(mBinding.root.context)
            mBinding.rvRecipes.adapter =
                RecipeItemAdapter()
        }
        selectedQuery = filterItemSelection
    }

    private fun getSearchOptions(): ArrayList<String> {
        return arrayListOf(
            "carrot",
            "broccoli",
            "asparagus",
            "cauliflower",
            "corn",
            "cucumber",
            "green pepper",
            "lettuce",
            "mushrooms",
            "onion",
            "potato",
            "pumpkin",
            "red pepper",
            "tomato",
            "beetroot",
            "brussel sprouts",
            "peas",
            "zucchini",
            "radish",
            "sweet potato",
            "artichoke",
            "leek",
            "cabbage",
            "celery",
            "chili",
            "garlic",
            "basil",
            "coriander",
            "parsley",
            "dill",
            "rosemary",
            "oregano",
            "cinnamon",
            "saffron",
            "green bean",
            "bean",
            "chickpea",
            "lentil",
            "apple",
            "apricot",
            "avocado",
            "banana",
            "blackberry",
            "blackcurrant",
            "blueberry",
            "boysenberry",
            "cherry",
            "coconut",
            "fig",
            "grape",
            "kiwifruit",
            "lemon",
            "lime",
            "lychee",
            "mandarin",
            "mango",
            "melon",
            "nectarine",
            "orange",
            "papaya",
            "passion fruit",
            "peach",
            "pear",
            "pineapple",
            "plum",
            "pomegranate",
            "quince",
            "raspberry",
            "strawberry",
            "watermelon",
            "salad",
            "pizza",
            "pasta",
            "popcorn",
            "lobster",
            "steak",
            "bbq",
            "pudding",
            "hamburger",
            "pie",
            "cake",
            "sausage",
            "tacos",
            "kebab",
            "poutine",
            "seafood",
            "chips",
            "fries",
            "masala",
            "paella",
            "som tam",
            "chicken",
            "toast",
            "marzipan",
            "tofu",
            "ketchup",
            "hummus",
            "chili",
            "maple syrup",
            "parma ham",
            "fajitas",
            "champ",
            "lasagna",
            "poke",
            "chocolate",
            "croissant",
            "arepas",
            "bunny chow",
            "pierogi",
            "donuts",
            "rendang",
            "sushi",
            "ice cream",
            "duck",
            "curry",
            "beef",
            "goat",
            "lamb",
            "turkey",
            "pork",
            "fish",
            "crab",
            "bacon",
            "ham",
            "pepperoni",
            "salami",
            "ribs"
        )
    }


}